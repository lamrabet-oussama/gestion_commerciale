import { createAction, props } from "@ngrx/store";
import {ArticleDto} from "src/app/api-client";

export const loadMesArticles=createAction('[MesArticles] Load Mes Articles');

export const loadMesArticlesSuccess=createAction('[MesArticles] Load Articles Success',props<{list:ArticleDto[]}>())

export const loadMesArticlesFailure=createAction('[MesArticles] Load Articles Failure',props<{error:any}>())
