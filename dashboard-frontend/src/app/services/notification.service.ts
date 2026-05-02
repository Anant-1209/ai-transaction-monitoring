import { Injectable } from '@angular/core';
import { Subject, Observable } from 'rxjs';
import { FraudAlert } from '../models/transaction.model';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private socket: WebSocket | null = null;
  private alertSubject = new Subject<FraudAlert>();

  constructor() {
    this.connect();
  }

  private connect() {
    this.socket = new WebSocket('ws://localhost:8083/alerts');

    this.socket.onmessage = (event) => {
      const alert: FraudAlert = JSON.parse(event.data);
      this.alertSubject.next(alert);
    };

    this.socket.onclose = () => {
      setTimeout(() => this.connect(), 3000); // Reconnect after 3s
    };
  }

  getAlerts(): Observable<FraudAlert> {
    return this.alertSubject.asObservable();
  }
}
