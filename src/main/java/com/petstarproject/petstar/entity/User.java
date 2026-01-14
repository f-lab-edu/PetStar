package com.petstarproject.petstar.entity;

import com.petstarproject.petstar.enums.UserRole;
import com.petstarproject.petstar.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name="users",
        uniqueConstraints = @UniqueConstraint(name="uk_users_email", columnNames="email")
)
public class User {

    @Id
    private String id; // UUID(PK)

    @Column(nullable = false, length = 120)
    private String email;

    @Column(name = "display_name", nullable = false, length = 30)
    private String displayName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatus status;

    @Column(columnDefinition = "TEXT")
    private String bio;

    // todo: private String password; 추가 예정


    public static User create(
            String email,
            String displayName,
            String bio
    ) {
        User user = new User();
        user.id = java.util.UUID.randomUUID().toString();
        user.email = email;
        user.displayName = displayName;
        user.bio = bio;

        user.role = UserRole.USER;
        user.status = UserStatus.ACTIVE;

        return user;
    }

    public void updateDisplayName(String displayName) {
        if (displayName != null && !displayName.isBlank()) {
            this.displayName = displayName;
        }
    }


    public void updateBio(String bio) {
        if (bio == null) return;
        this.bio = bio;
    }


    public void softDelete() {
        this.status = UserStatus.DELETED;
    }
}
