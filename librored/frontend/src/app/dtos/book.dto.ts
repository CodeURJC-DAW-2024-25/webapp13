export interface BookDTO {
  id?: number;
  title: string;
  description: string;
  author?: string;
  genre?: string;
  hasCoverImage: boolean;
  owner?: {
    id: number;
    username: string;
  };
  shops?: ShopBasicDTO[];
  isCurrentlyOnLoan?: boolean;
  currentLoanInfo?: {
    borrower: string;
    startDate: string;
    endDate?: string;
  };
}

export interface ShopBasicDTO {
  id: number;
  name: string;
  address: string;
}