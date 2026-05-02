import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { TransactionService } from './services/transaction.service';
import { NotificationService } from './services/notification.service';
import { Transaction, FraudAlert } from './models/transaction.model';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  transactions: Transaction[] = [];
  newTransaction: Transaction = {
    amount: 0,
    currency: 'USD',
    merchant: '',
    cardNumber: ''
  };
  
  selectedTransaction: Transaction | null = null;
  alerts: FraudAlert[] = [];

  constructor(
    private transactionService: TransactionService,
    private notificationService: NotificationService
  ) {}

  ngOnInit() {
    this.loadTransactions();
    
    // Listen for real-time fraud alerts
    this.notificationService.getAlerts().subscribe(alert => {
      this.alerts.unshift(alert);
      // Update local transaction list if match found
      const index = this.transactions.findIndex(t => t.id === alert.transactionId);
      if (index !== -1) {
        this.transactions[index].status = alert.status;
        this.transactions[index].riskScore = alert.riskScore;
        this.transactions[index].fraudExplanation = alert.fraudExplanation;
      }
    });
  }

  loadTransactions() {
    this.transactionService.getTransactions().subscribe(data => {
      this.transactions = data.reverse();
    });
  }

  submitTransaction() {
    this.transactionService.createTransaction(this.newTransaction).subscribe(res => {
      this.transactions.unshift(res);
      this.newTransaction = { amount: 0, currency: 'USD', merchant: '', cardNumber: '' };
    });
  }

  viewDetails(t: Transaction) {
    this.selectedTransaction = t;
  }

  closeModal() {
    this.selectedTransaction = null;
  }

  getRiskColor(score: number | undefined): string {
    if (!score) return 'transparent';
    if (score < 0.3) return '#22c55e';
    if (score < 0.7) return '#f59e0b';
    return '#ef4444';
  }
}
