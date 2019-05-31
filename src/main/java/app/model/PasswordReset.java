package app.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Password_Reset")
public class PasswordReset {

   @Id
   @Column(name = "user_id")
   private Integer userId;

   @JsonIgnore
   @ToString.Exclude
   @Column(unique = true)
   private String token;

   @Column
   private ZonedDateTime expires;

   @Setter(AccessLevel.NONE)
   @JsonIgnore
   @OneToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "user_id", referencedColumnName = "id", updatable = false, insertable = false)
   private User user;
}
