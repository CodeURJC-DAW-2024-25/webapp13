export interface BookDTO {
  id?: number;
  title: string;
  description: string;
  image: boolean;
  shops?: ShopBasicDTO[];
}

export interface ShopBasicDTO {
  id: number;
  name: string;
  address: string;
}