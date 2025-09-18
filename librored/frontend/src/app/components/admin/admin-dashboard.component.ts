import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AdminService } from '../../services/admin.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-admin-dashboard',
  templateUrl: './admin-dashboard.component.html'
})
export class AdminDashboardComponent implements OnInit {

  constructor(
    private router: Router,
    private adminService: AdminService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    // NO AUTH CHECK - Let backend handle it
    console.log('AdminDashboard loaded without auth check!');
  }

  navigateToUserManagement(): void {
    this.router.navigate(['/admin/users']);
  }

  navigateToBookManagement(): void {
    this.router.navigate(['/books']);
  }

  navigateToLoanManagement(): void {
    this.router.navigate(['/loans']);
  }

  downloadReport(): void {
    this.adminService.downloadAdminReport().subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'Admin_Report.pdf';
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);
      },
      error: (error) => {
        console.error('Error downloading report:', error);
        if (error.status === 401 || error.status === 403) {
          alert('You do not have permission to download the admin report. Please ensure you are logged in as an administrator.');
        } else {
          alert('Failed to download report. Please try again.');
        }
      }
    });
  }

  logout(): void {
    this.authService.logOut();
  }
}