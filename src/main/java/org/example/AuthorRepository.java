package org.example;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Repository;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface AuthorRepository extends CrudRepository<Author, String> {

}
