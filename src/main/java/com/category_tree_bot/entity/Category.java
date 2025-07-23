package com.category_tree_bot.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность {@code Category} представляет собой элемент дерева категорий,
 * который может быть как корневым, так и дочерним.
 *
 * <p>Каждая категория содержит уникальное имя, может иметь родительскую категорию
 * (связь ManyToOne) и список дочерних категорий (связь OneToMany).
 * Также содержит служебные поля {@code code} и {@code parentCode} — для импорта/экспорта из Excel.</p>
 *
 * <p>Сущность аннотирована JPA-аннотациями для хранения в таблице {@code categories}.</p>
 *
 * @author Dinara
 * @see com.category_tree_bot.repository.CategoryRepository
 */
@Entity
@Table(name = "categories")
public class Category {

    /**
     * Уникальный идентификатор категории.
     * Генерируется автоматически.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Название категории.
     * Должно быть уникальным и не может быть {@code null}.
     */
    @Column(nullable = false, unique = true)
    private String name;

    /**
     * Родительская категория (если есть).
     * Связь ManyToOne, используется для построения иерархии.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    /**
     * Список дочерних категорий.
     * Связь OneToMany, управляется полем {@code parent} в дочерних объектах.
     * Каскадное удаление и orphan removal обеспечивают удаление подкатегорий при удалении родителя.
     */
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Category> children = new ArrayList<>();

    /**
     * Код категории, используется при импорте/экспорте Excel.
     */
    private String code;

    /**
     * Код родительской категории, используется при импорте/экспорте Excel.
     */
    private String parentCode;

    /**
     * Конструктор по умолчанию.
     */
    public Category() {
    }

    /**
     * Конструктор с указанием имени категории.
     *
     * @param name название категории
     */
    public Category(String name) {
        this.name = name;
    }

    // Геттеры и сеттеры

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getParent() {
        return parent;
    }

    public void setParent(Category parent) {
        this.parent = parent;
    }

    public List<Category> getChildren() {
        return children;
    }

    public void setChildren(List<Category> children) {
        this.children = children;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }
}
