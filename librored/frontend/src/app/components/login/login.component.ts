import { Component, TemplateRef, ViewChild } from "@angular/core";
import { AuthService } from "../../services/auth.service";
import { NgbModal } from "@ng-bootstrap/ng-bootstrap";
import { Router } from "@angular/router";

@Component({
  selector: "login",
  templateUrl: "./login.component.html",
})
export class LoginComponent {
  @ViewChild("loginErrorModal")
  public loginErrorModal: TemplateRef<void> | undefined;

  constructor(
    public authService: AuthService,
    private modalService: NgbModal,
    private router: Router
  ) {}

  public logIn(user: string, pass: string) {
    this.authService.logIn(user, pass).subscribe({
      next: (response) => {
        console.log("Login successful:", response);
        // Redirect to intended page
        this.router.navigate(['/books']);
      },
      error: (error) => {
        console.error("Login failed:", error);
        if (this.loginErrorModal) {
          this.modalService.open(this.loginErrorModal, { centered: true });
        }
      }
    });
  }

  public logOut() {
    this.authService.logOut();
  }
}