package com.siit.ticketist.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "MediaFiles")
@Getter @Setter @NoArgsConstructor
public class MediaFile {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(nullable = false)
   private String fileName;

   public MediaFile(String fileName) {
      this.fileName = fileName;
   }
}