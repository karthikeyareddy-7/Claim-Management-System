package com.example.claim_management_system.Claim_Management_System.Objects;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "claims")
public class Claim {

	public Claim() {
		
	}
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int claimid;

    @Column(nullable = false)
    private String descriptioncode;

    @Column(nullable = false)
    private String procedurecode;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "date_submitted")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime dateSubmitted;

    @ManyToOne
    @JoinColumn(name = "userid", nullable = false)
    @JsonBackReference
    private User user;

	public int getClaimid() {
		return claimid;
	}

	public void setClaimid(int claimid) {
		this.claimid = claimid;
	}

	public String getDescriptioncode() {
		return descriptioncode;
	}

	public void setDescriptioncode(String descriptioncode) {
		this.descriptioncode = descriptioncode;
	}

	public String getProcedurecode() {
		return procedurecode;
	}

	public void setProcedurecode(String procedurecode) {
		this.procedurecode = procedurecode;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public LocalDateTime getDateSubmitted() {
		return dateSubmitted;
	}

	public void setDateSubmitted(LocalDateTime dateSubmitted) {
		this.dateSubmitted = dateSubmitted;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

    
}
