package uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.OnboardingList;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;

@Data
@AllArgsConstructor
@Entity
@Table(name = "onboarding_employees")
public class OnboardingEmployees {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotEmpty(message = "First name cannot be empty")
	@Length(max = 50, message = "First name must be less than 50 characters")
	@Column
	private String firstName;

	@NotEmpty(message = "Last name cannot be empty")
	@Length(max = 50, message = "Last name must be less than 50 characters")
	@Column
	private String lastName;

	@Column
	@NotEmpty(message = "Email cannot be empty")
	@Email (message = "Email invalid")
	private String emailAddress;

	@Column
	@Length(max = 128, message = "Address must be less than 128 characters")
	private String address;

	@Column
	@Length(max = 20, message = "Phone number must be less than 20 characters")
	private String phoneNumber;

	@Column
	@Length(max = 128, message = "Emergency contact name must be less than 128 characters")
	private String emergencyContactName;

	@Column
	@Length(max = 20, message = "Emergency contact phone number must be less than 20 characters")
	private String emergencyContactPhoneNumber;

	@Column
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	@Past(message = "Date of birth must be in the past")
	private LocalDate dateOfBirth;

	@Column
	@Range(min = 100000, max = 999999, message = "Bank sort code must be 6 digits")
	private Long bankSortCode;

	@Column
	@Range(min=10000000, max=99999999, message = "Bank account number must be 8 digits")
	private Long bankAccountNo;

	@Column
	@Pattern(regexp = "^$|^[A-Z]{2}[0-9]{6}[A-Z]$", message = "National insurance number must be in the format XX000000X")
	private String nationalInsuranceNo;

	@Column
	@Length(max = 128, message = "Role must be less than 128 characters")
	private String role;

	@Column
	@Pattern(regexp = "^(Full Time|Part Time|Contractor|Intern)$", message = "Employment status must be Full Time, Part Time, Contractor or Intern")
	private String employmentStatus;

	@Column
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate startDate;

	@Column
	@Length(max = 2048, message = "Notes must be less than 2048 characters")
	private String Notes;

	@Column
	private Boolean inductionComplete;

	@Column(name = "image_file_path")
	private String imageFilePath;

	@Column(name = "task_creation_email")
	private Boolean taskCreationEmail;

	@Column(name = "task_edit_email")
	private Boolean taskEditEmail;

	@Column(name = "task_reminder_email")
	private Boolean taskReminderEmail;
	// getters and setters


	// constructors
	public OnboardingEmployees() {
		this(0L, "", "", "", null, null, null, null, null, null, null, null, null, null, null, null, false, "", false, false, false);

	}
}
