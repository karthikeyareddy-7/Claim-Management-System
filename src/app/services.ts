import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Claim } from './components/claims';
import { User } from './components/users';
import assets from './components/assets.json';
@Injectable({
  providedIn: 'root'
})

export class Services {
  private baseUrl = assets.userApiUrl; // Backend root for users
  private baseUrlClaim = assets.claimsApiUrl;// Backend root for claims

  constructor(private http: HttpClient) {}


  // Register a new user
  registeruser(data: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/user/register`, data, {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' }),//frontend sends json
      responseType: 'text' // backend returns plain text
    });
  }

  // Login user
  loginuser(data: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/user/login`, data, {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' }),//frontend sends json 
      responseType: 'text' // backend returns "Login successful" text
    });
  }

  getUserByUsername(username : string): Observable<any>{
    return this.http.get(`${this.baseUrl}/user/${username}`);
  }

  // Get all claims by user
  getClaimsByUser(userId: number): Observable<Claim[]> {
    return this.http.get<Claim[]>(`${this.baseUrlClaim}/user/${userId}`);
  }

  // Add claim
  addClaim(userId: number,claim: Partial<Claim>): Observable<Claim> {
    return this.http.post<Claim>(`${this.baseUrlClaim}/user/${userId}`, claim, {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' })
    });
  }

  // Update claim
  updateClaim(claim: Claim): Observable<Claim> {
    return this.http.put<Claim>(`${this.baseUrlClaim}/${claim.claimid}`, claim, {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' })
    });
  }

  //Delete claim
 deleteClaim(claimId: number): Observable<string> {
  return this.http.delete(`${this.baseUrlClaim}/${claimId}`, {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
    responseType: 'text' 
  });
}

  private username: string = '';

  setUsername(name: string) {
    this.username = name;
  }

  getUsername(): string {
    return this.username;
  }

}
