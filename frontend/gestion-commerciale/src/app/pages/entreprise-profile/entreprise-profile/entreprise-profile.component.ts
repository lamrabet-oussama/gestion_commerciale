import { Component, OnInit } from '@angular/core';
import { MesInfoxDto } from 'src/app/api-client';
import { EntrepriseProfileService } from 'src/app/services/entreprise-profile/entreprise-profile.service';
import { NotificationService } from 'src/app/services/notification/notification.service';

@Component({
  selector: 'app-entreprise-profile',
  templateUrl: './entreprise-profile.component.html',
  styleUrls: ['./entreprise-profile.component.scss'],
})
export class EntrepriseProfileComponent implements OnInit {
  mesInfos: MesInfoxDto = {};
  selectedFile: File | null = null;
  uploadedUrl: string | null = null;
  constructor(
    private mesInfosSerice: EntrepriseProfileService,
    private notification: NotificationService
  ) {}

  ngOnInit(): void {
      this.getInfos();
  }
  onFileSelected(event: any) {
    this.selectedFile = event.target.files[0];
  }
  updateInfos() {
    console.log(this.mesInfos);
    this.mesInfosSerice
      .updateInfos(this.mesInfos, this.selectedFile)
      .subscribe({
        next: (res) => {
          // Optionnel : mettre à jour l'UI, recharger les infos, etc.
          console.log('Mise à jour réussie', res);
          this.uploadedUrl = res?.blogo ?? null;
          // notifier
          this.notification.success('Mise à jour réussie');
                this.getInfos();

        },
        error: (err) => {
          console.error('Erreur lors de la mise à jour', err);
          // notifier
          this.notification.error(err.error?.errors,'Erreur');
        },
      });
  }

  getInfos(){
    this.mesInfosSerice.getInfos().subscribe({
      next: (res) => {
        this.mesInfos = res;
        console.log('Infos récupérées', this.mesInfos);
        this.uploadedUrl = res?.blogo ?? null;
      },
      error: (err) => {
        console.error('Erreur lors de la récupération des infos', err);
        // notifier
        this.notification.error(err.error?.errors,'Erreur');
      },
    });
  }
}
