package com.siit.ticketist.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * Represents a single media file (photo, video, etc.)
 */
@Entity
@Table(name = "MediaFiles")
@Getter @Setter @NoArgsConstructor
public class MediaFile {

   /**
    * Unique identifier of the media file
    */
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   /**
    * Name of the media file
    */
   @Column(nullable = false)
   private String fileName;

   /**
    * MIME type of the media file (used to identify the type of data)
    */
   @Column(nullable = false)
   private String mimeType;

   public MediaFile(String fileName, String mimeType) {
      this.fileName = fileName;
      this.mimeType = mimeType;
   }
}