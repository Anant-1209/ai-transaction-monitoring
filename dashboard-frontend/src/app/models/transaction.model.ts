export interface Transaction {
  id?: number;
  amount: number;
  currency: string;
  merchant: string;
  cardNumber: string;
  timestamp?: string;
  status?: string;
  riskScore?: number;
  fraudExplanation?: string;
}

export interface FraudAlert {
  transactionId: number;
  amount: number;
  merchant: string;
  riskScore: number;
  status: string;
  fraudExplanation: string;
}
