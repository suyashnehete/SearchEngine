import { UrlResponse } from "./url-response";

export interface SearchResponse {

        documents: UrlResponse[];
        totalResults: number;
        totalPages: number;
        currentPage: number;
        pageSize: number;

  }