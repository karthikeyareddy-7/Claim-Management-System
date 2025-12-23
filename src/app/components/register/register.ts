import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink, RouterModule } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';
import { Services } from '../../services';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule, RouterLink, RouterModule],
  templateUrl: './register.html',
  styleUrls: ['./register.css'],
})
export class Register {
  username: string = '';
  password: string = '';
  confirmPassword: string = '';
  dob : string = '';
  email : string = '';
  gender : string = '';
  firstname : string = '';
  lastname : string = '';
  today: any;

  constructor(private api: Services, private router: Router) {}

  register(): void {
    if (!this.username || !this.password || !this.confirmPassword || !this.dob || !this.email || !this.gender || !this.firstname || !this.lastname ) {
      alert('Please fill all fields');
      return;
    }

    if (!/^[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,4}$/i.test(this.email)) {
      alert('Invalid email!');
      return;
    }

    const dob = new Date(this.dob); 
    const today = new Date();
    const age = today.getFullYear() - dob.getFullYear();
    const monthDiff = today.getMonth() - dob.getMonth();
    const dayDiff = today.getDate() - dob.getDate();

    if (age < 18 || (age === 18 && (monthDiff < 0 || (monthDiff === 0 && dayDiff < 0)))) {
        alert('You must be at least 18 years old');
        return;
    }


    if (this.password !== this.confirmPassword) {
      alert('Passwords do not match');
      return;
    }

    const data = { username : this.username.toLowerCase(), password : this.password, dob : this.dob, email : this.email, gender :
      this.gender, firstname : this.firstname , lastname : this.lastname};

    this.api.registeruser(data).subscribe({
      next: (response: any) => {
        alert('User registered successfully!');
        this.router.navigate(['/login']);
      },
      error: (err) => {
        console.error('Registration failed', err);
        alert(err.error || 'Registration failed');
      }
    });
  }
}
