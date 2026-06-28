import { Component, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { finalize } from 'rxjs';
import { AuthService } from '../../core/auth.service';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrl: './login.scss'
})
export class Login {
  readonly loading = signal(false);
  readonly error = signal('');
  readonly form;

  constructor(
    private readonly formBuilder: FormBuilder,
    private readonly auth: AuthService,
    private readonly router: Router
  ) {
    this.form = this.formBuilder.nonNullable.group({
      documentId: ['', [Validators.required, Validators.minLength(11)]],
      password: ['', [Validators.required, Validators.minLength(8)]]
    });
  }

  submit(): void {
    if (this.form.invalid || this.loading()) {
      this.form.markAllAsTouched();
      return;
    }
    this.loading.set(true);
    this.error.set('');
    const { documentId, password } = this.form.getRawValue();
    this.auth.login(documentId, password).pipe(
      finalize(() => this.loading.set(false))
    ).subscribe({
      next: () => void this.router.navigate(['/chat']),
      error: () => this.error.set('Documento ou senha inválidos. Confira os dados e tente novamente.')
    });
  }
}
