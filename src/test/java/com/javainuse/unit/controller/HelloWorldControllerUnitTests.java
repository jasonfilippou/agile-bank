package com.javainuse.unit.controller;


import static org.junit.jupiter.api.Assertions.assertEquals;

import com.javainuse.controller.HelloWorldController;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class HelloWorldControllerUnitTests {
    private HelloWorldController helloWorldController;
    @Test
    public void testAuthenticatedHelloWorld(){
        assertEquals("Hello World!", helloWorldController.firstPage() );
    }
    
  
}
