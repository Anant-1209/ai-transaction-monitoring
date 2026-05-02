import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  transactions: any[] = [];
  newTx = { merchant: '', amount: 0 };
  selectedTx: any = null;
  fraudCount = 0;
  
  private socket: WebSocket | null = null;
  private apiUrl = 'http://localhost:8080/transactions'; // Through Gateway

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.loadTransactions();
    this.connectWebSocket();
  }

  loadTransactions() {
    this.http.get<any[]>(this.apiUrl).subscribe(data => {
      this.transactions = data;
      this.updateStats();
    });
  }

  connectWebSocket() {
    // Connect to the Notification Service
    this.socket = new WebSocket('ws://localhost:8083/alerts');
    
    this.socket.onmessage = (event) => {
      const alert = JSON.parse(event.data);
      // Map incoming alert data to transaction structure
      const updatedTx = {
        id: alert.transactionId,
        merchant: alert.merchant,
        amount: alert.amount,
        riskScore: alert.riskScore,
        status: alert.status,
        fraudExplanation: alert.fraudExplanation
      };

      const index = this.transactions.findIndex(t => t.id === updatedTx.id);
      if (index !== -1) {
        // Create a new array reference to trigger Angular change detection
        this.transactions[index] = updatedTx;
        this.transactions = [...this.transactions];
      } else {
        this.transactions = [...this.transactions, updatedTx];
      }
      this.updateStats();
    };

    this.socket.onclose = () => {
      setTimeout(() => this.connectWebSocket(), 3000);
    };
  }

  createTransaction() {
    this.http.post(this.apiUrl, this.newTx).subscribe((res: any) => {
      this.transactions.push(res);
      this.newTx = { merchant: '', amount: 0 };
      this.updateStats();
    });
  }

  updateStats() {
    this.fraudCount = this.transactions.filter(t => t.status === 'FRAUD_FLAGGED').length;
  }

  showAiModal(tx: any) {
    this.selectedTx = tx;
  }
}
