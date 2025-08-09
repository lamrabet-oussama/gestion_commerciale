import { Component, OnInit } from '@angular/core';
import { CaisseJourDto, UserDto } from 'src/app/api-client';
import { CaisseJourService } from 'src/app/services/caisse-jour-service/caisse-jour.service';
import { NotificationService } from 'src/app/services/notification/notification.service';

@Component({
  selector: 'app-page-caisse-jour',
  templateUrl: './page-caisse-jour.component.html',
  styleUrls: ['./page-caisse-jour.component.scss'],
})
export class PageCaisseJourComponent implements OnInit {
  caisseJourDto: CaisseJourDto = {};
  selectedDate: string = '';
  selectedUser:UserDto={};
  minDate: string = '';
  maxDate: string = '';

  users:UserDto[]=[];
  public constructor(
    private caisseJourService: CaisseJourService,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    this.initializeDateLimits();
    this.setTodayAsDefault();
    this.getUsers();
  }

  private initializeDateLimits(): void {
    

    // Date maximum : aujourd'hui
    const today = new Date();
    this.maxDate = this.formatDateForInput(today);
  }

  private setTodayAsDefault(): void {
    const today = new Date();
    this.selectedDate = this.formatDateForInput(today);

    // Charger automatiquement les données d'aujourd'hui
    this.getCaisseJour();
  }

  private formatDateForInput(date: Date): string {
    return date.toISOString().split('T')[0]; // yyyy-mm-dd
  }

  private formatDateForDisplay(dateString: string): string {
    if (!dateString) return '';

    const date = new Date(dateString);
    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const year = date.getFullYear();

    return `${day}/${month}/${year}`;
  }

  getUsers(): void {
    this.caisseJourService.getUsers().subscribe({
      next: (users) => {
        this.users = users;
        console.log('Utilisateurs chargés:', this.users);
      },
      error: (error) => {
        this.notificationService.error('Erreur lors du chargement des utilisateurs', 'Erreur');
        this.users = [];
      }
    });
  }

  onDateChange(): void {
    if (this.selectedDate) {
      // Validation de la date
      if (this.isValidDate(this.selectedDate)) {
        this.getCaisseJour();
      } else {
        this.notificationService.error(
          `La date doit être comprise entre le ${this.formatDateForDisplay(
            this.minDate
          )} et le ${this.formatDateForDisplay(this.maxDate)}`,
          'Date invalide'
        );
        // Réinitialiser à aujourd'hui en cas de date invalide
        this.setTodayAsDefault();
      }
    } else {
      // Réinitialiser les données si aucune date n'est sélectionnée
      this.caisseJourDto = {};
    }
  }

  onUserSelected(user: UserDto): void {
    this.selectedUser = user;
    this.getCaisseJour();
  }
  private isValidDate(dateString: string): boolean {
    const selectedDate = new Date(dateString);
    const maxDate = new Date(this.maxDate);

    return  selectedDate <= maxDate;
  }

  getCaisseJour(): void {
    if (!this.selectedDate) {
      this.notificationService.error(
        'Veuillez sélectionner une date',
        'Erreur'
      );
      return;
    }

    // Convertir la date au format ISO DateTime requis par le backend
    const startDateTime = `${this.selectedDate}T00:00:00`;
    const endDateTime = `${this.selectedDate}T23:59:59`;

    console.log(
      `Chargement des données pour le ${this.formatDateForDisplay(
        this.selectedDate
      )}...`
    );

    this.caisseJourService
      .getCaisseJour(this.selectedUser.cod, startDateTime)
      .subscribe({
        next: (caisseJour) => {
          this.caisseJourDto = caisseJour;
          console.log('Caisse du jour:', caisseJour, this.selectedUser);

          // Notification de succès optionnelle
          this.notificationService.success(
            `Données chargées pour le ${this.formatDateForDisplay(
              this.selectedDate
            )}`,
            'Succès'
          );
        },
        error: (error) => {
          console.error('Erreur API:', error);
          this.caisseJourDto = {}; // Réinitialiser en cas d'erreur

          let errorMessage =
            'Erreur lors de la récupération de la caisse du jour';

          // Personnaliser le message d'erreur selon le code d'erreur
          if (error.status === 404) {
            errorMessage = `Aucune donnée trouvée pour le ${this.formatDateForDisplay(
              this.selectedDate
            )}`;
          } else if (error.status === 400) {
            errorMessage = 'Format de date invalide';
          } else if (error.status === 500) {
            errorMessage = 'Erreur serveur. Veuillez réessayer plus tard.';
          }

          this.notificationService.error(errorMessage, 'Erreur');
        },
      });
  }

  // Méthodes utilitaires pour les boutons de navigation rapide (optionnelles)
  selectToday(): void {
    const today = new Date();
    this.selectedDate = this.formatDateForInput(today);
    this.getCaisseJour();
  }

  selectYesterday(): void {
    const yesterday = new Date();
    yesterday.setDate(yesterday.getDate() - 1);

    if (this.isValidDate(this.formatDateForInput(yesterday))) {
      this.selectedDate = this.formatDateForInput(yesterday);
      this.getCaisseJour();
    } else {
      this.notificationService.error(
        "La date d'hier n'est pas dans la plage autorisée",
        'Date invalide'
      );
    }
  }

  selectPreviousDay(): void {
    if (!this.selectedDate) return;

    const currentDate = new Date(this.selectedDate);
    currentDate.setDate(currentDate.getDate() - 1);

    if (this.isValidDate(this.formatDateForInput(currentDate))) {
      this.selectedDate = this.formatDateForInput(currentDate);
      this.getCaisseJour();
    } else {
      this.notificationService.error(
        'Impossible de naviguer plus loin dans le passé',
        'Limite atteinte'
      );
    }
  }

  selectNextDay(): void {
    if (!this.selectedDate) return;

    const currentDate = new Date(this.selectedDate);
    currentDate.setDate(currentDate.getDate() + 1);

    if (this.isValidDate(this.formatDateForInput(currentDate))) {
      this.selectedDate = this.formatDateForInput(currentDate);
      this.getCaisseJour();
    } else {
      this.notificationService.error(
        'Impossible de naviguer dans le futur',
        'Limite atteinte'
      );
    }
  }

  // Méthode pour vérifier si c'est aujourd'hui (pour styling conditionnel)
  isToday(): boolean {
    const today = new Date();
    return this.selectedDate === this.formatDateForInput(today);
  }

  // Méthode pour obtenir le nom du jour en français
  getDayName(): string {
    if (!this.selectedDate) return '';

    const date = new Date(this.selectedDate);
    const options: Intl.DateTimeFormatOptions = { weekday: 'long' };
    return date.toLocaleDateString('fr-FR', options);
  }

  downloadCaisseJourPdf(){
    this.caisseJourService.downloadCaisseJour(this.selectedUser.cod,`${this.selectedDate}T00:00:00`
).subscribe({
      next:(pdf:Blob)=>{
        const fileURL=URL.createObjectURL(pdf);
         window.open(fileURL);
       
      },
      error:err=>{
        console.log(err);
        this.notificationService.error(err.error.message,"Erreur")
      }
    })
  }
}
