export * from './articles.service';
import { ArticlesService } from './articles.service';
export * from './mesInfos.service';
import { MesInfosService } from './mesInfos.service';
export * from './tiers.service';
import { TiersService } from './tiers.service';
export const APIS = [ArticlesService, MesInfosService, TiersService];
