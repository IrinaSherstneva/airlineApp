package at.qe.sepm.skeleton.repositories;

import at.qe.sepm.skeleton.model.User;
import at.qe.sepm.skeleton.model.UserRole;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository for managing {@link User} entities.
 *
 * This class is part of the skeleton project provided for students of the
 * courses "Software Architecture" and "Software Engineering" offered by the
 * University of Innsbruck.
 */
public interface UserRepository extends AbstractRepository<User, String> {

	//@Query("SELECT u FROM User u WHERE u.username = :uname AND u.activeUser = true")
    User findFirstByUsername(String username);

	/*@Query("SELECT u FROM User u WHERE CONTAINS(u.username, :uname) AND u.activeUser = true")
    List<User> findByUsernameContaining(@Param("uname") String username);*/

    @Query("SELECT u FROM User u WHERE CONCAT(u.firstName, ' ', u.lastName) = :wholeName AND u.activeUser = true")
    List<User> findByWholeNameConcat(@Param("wholeName") String wholeName);

    @Query("SELECT u FROM User u WHERE :role MEMBER OF u.roles AND u.activeUser = true")
    List<User> findByRole(@Param("role") UserRole role);
    
    @Query("SELECT u FROM User u WHERE u.activeUser = true")
    List<User> findAll();
    
    @Query("SELECT u.username FROM User u")
    List<String> findAllUsernames();

}
