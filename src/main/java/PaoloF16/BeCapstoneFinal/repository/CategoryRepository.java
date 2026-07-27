package PaoloF16.BeCapstoneFinal.repository;

import PaoloF16.BeCapstoneFinal.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
}
