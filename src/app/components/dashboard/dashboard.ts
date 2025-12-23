import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ClaimWithSelection } from '../ClaimWithSelection';
import { User } from '../users';
import { Services } from '../../services';
import { HttpClientModule } from '@angular/common/http';
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule],
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.css'],
})
export class Dashboard implements OnInit {

  claims: ClaimWithSelection[] = [];
  filteredClaims: ClaimWithSelection[] = [];

  newClaim: Partial<ClaimWithSelection> = {};
  editingClaim: ClaimWithSelection | null = null;

  userid!: number;
  username: string = '';
  user: User | null = null;
  searchText: string = '';

  // Sorting
  sortColumn: keyof ClaimWithSelection | null = null;
  sortDirection: 'asc' | 'desc' = 'asc';

  // Filters
  descriptionFilter = '';
  procedureFilter = '';
  priceFilter = '';
  dateFilter = '';

  constructor(private service: Services, private router: Router) {}

  ngOnInit(): void {
    this.username = this.service.getUsername() || '';

    if (!this.username) {
      alert('Please login first!');
      this.router.navigate(['/login']);
      return;
    }

    this.service.getUserByUsername(this.username).subscribe({
      next: (data) => {
        if (!data) {
          alert('User not found!');
          this.router.navigate(['/login']);
          return;
        }
        this.user = data;
        this.userid = data.userid;
        this.username = data.username;
        this.loadClaims();
      },
      error: (err) => {
        console.error('Error fetching user:', err);
        alert('Failed to fetch user data!');
        this.router.navigate(['/login']);
      },
    });
  }

  loadClaims(): void {
    this.service.getClaimsByUser(this.userid).subscribe({
      next: (data) => {
        this.claims = Array.isArray(data)
          ? data.map((c) => ({ ...c, selected: false }))
          : [];
        this.applyFiltersAndSorting();
      },
      error: (err) => {
        console.error('Error fetching claims:', err);
        this.claims = [];
        this.filteredClaims = [];
      },
    });
  }

  addClaim(): void {
    if (
      !this.newClaim.descriptioncode ||
      !this.newClaim.procedurecode ||
      this.newClaim.price === undefined
    ) {
      alert('Please fill all fields.');
      return;
    }

    if (this.newClaim.price > 5000) {
      alert('⚠ Price exceeds $5000, must be manually verified!');
    }

    this.service.addClaim(this.userid, this.newClaim).subscribe({
      next: (createdClaim) => {
        this.claims.push({ ...createdClaim, selected: false });
        this.newClaim = {};
        this.applyFiltersAndSorting();
      },
      error: (err) => {
        console.error('Error adding claim:', err);
        alert('Failed to save claim!');
      },
    });
  }

  editClaim(claim: ClaimWithSelection): void {
    this.editingClaim = { ...claim };
    this.applyFiltersAndSorting();
  }

  updateClaim(): void {
    if (!this.editingClaim) return;

    if (this.editingClaim.price > 5000) {
      alert('⚠ Price exceeds $5000, must be manually verified!');
    }

    this.service.updateClaim(this.editingClaim).subscribe({
      next: (updatedClaim) => {
        const index = this.claims.findIndex(
          (c) => c.claimid === updatedClaim.claimid
        );
        if (index !== -1) {
          this.claims[index] = {
            ...updatedClaim,
            selected: this.claims[index].selected,
          };
        }
        this.editingClaim = null;
        this.applyFiltersAndSorting();
      },
      error: (err) => {
        console.error('Error updating claim:', err);
        alert('Failed to update claim!');
      },
    });
  }

  cancelEdit(): void {
    this.editingClaim = null;
  }

  deleteClaim(claimid: number): void {
    this.service.deleteClaim(claimid).subscribe({
      next: () => {
        this.claims = this.claims.filter((c) => c.claimid !== claimid);
        this.applyFiltersAndSorting();
      },
      error: (err) => {
        console.error('Error deleting claim:', err);
        alert('Failed to delete claim!');
      },
    });
  }

  deleteSelectedClaims(): void {
    const selectedIds = this.claims.filter((c) => c.selected).map((c) => c.claimid);
    if (selectedIds.length === 0) return;
    if (!confirm(`Are you sure you want to delete ${selectedIds.length} selected claims?`))
      return;

    selectedIds.forEach((id) => this.deleteClaim(id));
  }

  // ✅ Called whenever any checkbox changes
  checkSelectionChange(): void {
    // This simply triggers Angular’s change detection for *ngIf on Delete Selected button
  }

  // ✅ Used by *ngIf in HTML to show/hide Delete Selected button
  hasSelectedClaims(): boolean {
    return this.filteredClaims?.some((claim) => claim.selected);
  }

  toggleSelectAll(event: any): void {
    const checked = event.target.checked;
    this.filteredClaims.forEach((c) => (c.selected = checked));
  }

  areAllSelected(): boolean {
    return this.filteredClaims.length > 0 && this.filteredClaims.every((c) => c.selected);
  }

  sortClaims(column: keyof ClaimWithSelection): void {
    if (this.sortColumn === column) {
      this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortColumn = column;
      this.sortDirection = 'asc';
    }
    this.applyFiltersAndSorting();
  }

  applyFiltersAndSorting(): void {
    this.filteredClaims = this.claims.filter(
      (claim) =>
        claim.descriptioncode
          .toLowerCase()
          .includes(this.descriptionFilter.toLowerCase()) &&
        claim.procedurecode
          .toLowerCase()
          .includes(this.procedureFilter.toLowerCase()) &&
        claim.price.toString().includes(this.priceFilter) &&
        claim.datesubmitted.toLowerCase().includes(this.dateFilter.toLowerCase())
    );

    if (this.sortColumn) {
      this.filteredClaims.sort((a, b) => {
        const valA = a[this.sortColumn!] as any;
        const valB = b[this.sortColumn!] as any;

        if (valA == null) return 1;
        if (valB == null) return -1;
        if (valA < valB) return this.sortDirection === 'asc' ? -1 : 1;
        if (valA > valB) return this.sortDirection === 'asc' ? 1 : -1;
        return 0;
      });
    }
  }

  getSortIcon(column: keyof ClaimWithSelection): string {
    if (this.sortColumn !== column) return '';
    return this.sortDirection === 'asc' ? '▲' : '▼';
  }

  trackByClaimId(index: number, claim: ClaimWithSelection) {
    return claim.claimid;
  }

  // ✅ PDF Export with proper types
  exportToPDF(): void {
    const doc = new jsPDF();

    doc.setFontSize(18);
    doc.text(`${this.username}'s Claims`, 14, 20);

    // Only pick fields needed for PDF
    type ClaimPDF = Pick<
      ClaimWithSelection,
      'descriptioncode' | 'procedurecode' | 'price' | 'datesubmitted'
    >;

    const columns: { header: string; dataKey: keyof ClaimPDF }[] = [
      { header: 'Description', dataKey: 'descriptioncode' },
      { header: 'Procedure', dataKey: 'procedurecode' },
      { header: 'Price', dataKey: 'price' },
      { header: 'Submitted On', dataKey: 'datesubmitted' },
    ];

    const rows: ClaimPDF[] = this.filteredClaims.map((c) => ({
      descriptioncode: c.descriptioncode,
      procedurecode: c.procedurecode,
      price: c.price,
      datesubmitted: new Date(c.datesubmitted).toLocaleString(),
    }));

    autoTable(doc, {
      head: [columns.map((col) => col.header)],
      body: rows.map((row) => columns.map((col) => row[col.dataKey])),
      startY: 30,
      styles: { fontSize: 10 },
      headStyles: { fillColor: [41, 128, 185], textColor: 255 },
    });

    doc.save(`${this.username}_claims.pdf`);
  }
}
