//package br.controller;
//
//import br.dto.request.LoginRequest;
//import br.service.AuthService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(AuthController.class)
//class AuthControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @MockBean
//    private AuthService authService;
//
//    @Test
//    void deveRetornarBadRequestQuandoLoginInvalido() throws Exception {
//
//        LoginRequest request =
//                new LoginRequest(
//                        "",
//                        "",
//                        ""
//                );
//
//        mockMvc.perform(
//                        post("/api/auth/login")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(
//                                        objectMapper.writeValueAsString(
//                                                request
//                                        )
//                                )
//                )
//                .andExpect(status().isBadRequest());
//
//    }
//
//}