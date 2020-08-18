package ai.bitflow.helppress.publisher.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Data
@ToString
@Entity
public class ReleaseHistory {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(insertable=false, updatable=false)
	private Integer id;
	@Column(length=10)
	private String type; // ALL, PART
	@Column(length=255)
	private String fileName;
	@Column(length=255)
	private String comment;
	
	@CreationTimestamp
    private LocalDateTime updDt;
	
	public String getUpdDtStr() {
		return updDt.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"));
	}
	
}
