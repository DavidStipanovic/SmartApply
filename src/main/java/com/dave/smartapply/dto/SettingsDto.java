package com.dave.smartapply.dto;

import org.springframework.web.multipart.MultipartFile;

public class SettingsDto {
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private String country;
    private String timezone;
    private String bioHtml; // Für den Rich-Text-Editor Inhalt

    // Wichtig: Dieses Feld nimmt die Datei beim Upload entgegen.
    // Es wird nicht in der DB gespeichert, sondern im Controller verarbeitet.
    private MultipartFile profileImageFile;

    // Konstruktoren
    public SettingsDto() {}

    // Getter und Setter
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }

    public String getBioHtml() { return bioHtml; }
    public void setBioHtml(String bioHtml) { this.bioHtml = bioHtml; }

    public MultipartFile getProfileImageFile() { return profileImageFile; }
    public void setProfileImageFile(MultipartFile profileImageFile) { this.profileImageFile = profileImageFile; }
}
