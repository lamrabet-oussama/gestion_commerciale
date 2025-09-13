import { ArticleDto } from "src/app/api-client";

export interface MesArticlesState{
  list:ArticleDto[]|[];
  error:any;
}

export const initialArticlesList:MesArticlesState={
  list: [],
  error: null
}
