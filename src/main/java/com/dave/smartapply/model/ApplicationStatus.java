package com.dave.smartapply.model;

public enum ApplicationStatus {

    DRAFT("Entwurf"),
    APPLIED("Beworben"),
    INTERVIEW_SCHEDULED("Vorstellungsgespräch geplant"),
    INTERVIEW_DONE("Vorstellungsgespräch erledigt"),
    OFFER_RECEIVED("Zusage erhalten"),
    ACCEPTED("Angenommen"),
    REJECTED("Abgelehnt");

    private final String displayName;

    ApplicationStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
