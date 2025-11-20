package IIS.wis2_backend.Enum;

/**
 * Enum for user roles.
 */
public enum Roles {
    /** Admin role with all permissions. */
    ADMIN,

    /** Standard user role with limited permissions. */
    USER,
    /** Teacher role with permissions to manage courses and students. */
    TEACHER
}