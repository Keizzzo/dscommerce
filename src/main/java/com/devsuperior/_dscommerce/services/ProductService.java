package com.devsuperior._dscommerce.services;

import com.devsuperior._dscommerce.dto.ProductDTO;
import com.devsuperior._dscommerce.entities.Product;
import com.devsuperior._dscommerce.repositories.ProductRepository;
import com.devsuperior._dscommerce.services.exceptions.DatabaseException;
import com.devsuperior._dscommerce.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id){

        Product result = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Recurso não encontrado."));
        return new ProductDTO(result);

    }

    // Pageable and Page import should be from springframework data domain
    @Transactional(readOnly = true)
    public Page<ProductDTO> findAll(Pageable pageable){
        // Page is already a stream() from Java
        Page<Product> result = repository.findAll(pageable);
        return result.map(product -> new ProductDTO(product));

    }

    @Transactional
    public ProductDTO insert(ProductDTO dto){
        // Page is already a stream() from Java
        Product entity = new Product();

        copyDtoToEntity(dto, entity);

        entity = repository.save(entity);
        return new ProductDTO(entity);

    }

    @Transactional
    public ProductDTO update(Long id, ProductDTO dto){

        try{
            // getReferenceById(): Essa operação não vai ao BD, ela acessa os valores de um
            // objeto pelo mapeamento JPA feito
            Product entity = repository.getReferenceById(id);

            copyDtoToEntity(dto, entity);

            entity = repository.save(entity);
            return new ProductDTO(entity);

        } catch (EntityNotFoundException e){
            throw new ResourceNotFoundException("Recurso não encontrado.");
        }

    }

    // SUPPORTS: só executa a transação se o método estiver no contexto de outra transação
    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Recurso não encontrado");
        }
        try {
            repository.deleteById(id);
        }
        catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Falha de integridade referencial");
        }
    }


    private void copyDtoToEntity(ProductDTO dto, Product entity) {

        entity.setName(dto.getName());
        entity.setDescription((dto.getDescription()));
        entity.setPrice(dto.getPrice());
        entity.setImgUrl(dto.getImgUrl());
    }

}
