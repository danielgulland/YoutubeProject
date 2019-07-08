package app.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Playlist")
public class Playlist {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Integer id;

   private String name;

   @Column(name = "user_id")
   private Integer userId;

   @Column(name = "total_listens")
   private Integer totalListens;

   @Column(name = "private")
   private boolean isPrivate;

   private String genre;

   @Column(name = "date_created")
   private ZonedDateTime dateCreated;

   @Setter(AccessLevel.NONE)
   @JsonIgnore
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "user_id", referencedColumnName = "id", updatable = false, insertable = false)
   private User user;

   @JsonIgnore
   @ManyToMany(cascade = {CascadeType.ALL})
   @JoinTable(name = "playlist_song", joinColumns = {@JoinColumn(name = "playlist_id")},
         inverseJoinColumns = {@JoinColumn(name = "song_id")})
   private List<Song> songs;
}