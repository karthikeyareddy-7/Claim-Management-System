import { Component } from '@angular/core';
import { Router, RouterLink, RouterOutlet } from '@angular/router';
import { Services } from '../../services';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpClientModule } from '@angular/common/http';


@Component({
  selector: 'app-login',
  standalone : true,
  imports: [CommonModule,FormsModule,RouterLink,HttpClientModule],
  templateUrl: './login.html',
  styleUrls: ['./login.css'],
})
export class Login {
  username = '';
  password = '';

  constructor(private api: Services, private router: Router) {}

  login(): void {
    if (!this.username || !this.password) {
      alert('Please enter username and password');
      return;
    }

    const payload = {
      username: this.username.toLowerCase(),
      password: this.password
    };

    console.log('Login payload:', payload);

    this.api.loginuser(payload).subscribe({
      next: (res) => {
        alert(res); 
      
        console.log('Successful Navigation');
        this.api.setUsername(this.username.toLowerCase());
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        console.error('Login failed:', err);
        alert(err.error || 'Login failed');
      }
    });
  }
}
