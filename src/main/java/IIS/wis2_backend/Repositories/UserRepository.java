package IIS.wis2_backend.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import IIS.wis2_backend.Models.User.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}