package com.ctang.zephyrcentrum.models;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class PostTest {
    
    @Test
    void testPostCreation() {
        Post post = new Post();
        post.setContent("Test content");
        post.setUser(new User());
        post.setCreatedDate(LocalDateTime.now());

        assertEquals("Test content", post.getContent());
        assertNotNull(post.getUser());
        assertNotNull(post.getCreatedDate());
    }

    @Test
    void testPostEquality() {
        Post post1 = new Post();
        post1.setContent("Test content");

        Post post2 = new Post();
        post2.setContent("Test content");

        assertEquals(post1, post2);
    }

    @Test
    void testPostInequality() {
        Post post1 = new Post();
        post1.setContent("Test content");

        Post post2 = new Post();
        post2.setContent("Different content");

        assertNotEquals(post1, post2);
    }

    @Test
    void testPostToString() {
        Post post = new Post(); 
        post.setContent("Test content");

        String expected = "Post{id=null, content='Test content', user=null, createdDate=null}";
        assertEquals(expected, post.toString());
    }
    
}
