package org.example.gamerent.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.example.gamerent.models.base.IdCreatedModified;


@Entity
@Table(name = "rentals")
public class Rental extends IdCreatedModified {



}