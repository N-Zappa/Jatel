package com.application.jatel.Models;


import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    @NotBlank(message = "The number of characters must not exceed 255")
    private String Title;
    @NotBlank(message = "The number of characters must not exceed 10000")
    private String Text;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User Author;
    private String UploadDate;


    public Post(String title, String text, User user) {
        Title = title;
        Text = text;
        this.Author = user;
    }
    public String getAuthorName()
    {
        return Author!=null ? Author.getNameOfUser():"Not mentioned";
    }
    public Long getAuthorsId()
    {
        return Author.getId();
    }
    public Post() {
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }

    public User getAuthor() {
        return Author;
    }

    public void setAuthor(User author) {
        Author = author;
    }

    public String getDate() {
        return UploadDate;
    }

    public void setDate(String uploadDate) {
        UploadDate = uploadDate;
    }
}
