package com.agilebank.config;

import static com.agilebank.util.Constants.SOURCE_ACCOUNT_ID;
import static com.agilebank.util.Constants.TARGET_ACCOUNT_ID;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.NumberSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "Agile Bank API", version = "${api.version}",
                contact = @Contact(name = "Jason Filippou", email = "jason.filippou@gmail.com", url = "https://www.jasonfilippou.com"),
                license = @License(name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0"), termsOfService = "${tos.uri}",
                description = "${api.description}"),
        servers = {
                @Server(url = "http://localhost:8080", description = "Development"),
                @Server(url = "${api.server.url}", description = "Production")})
public class OpenAPI30Configuration {

    /**
     * Configure the OpenAPI components.
     *
     * @return Returns fully configure OpenAPI object
     * @see OpenAPI
     */
    @Bean
    public OpenAPI customizeOpenAPI() {
        final String securitySchemeName = "bearerAuth";
    return new OpenAPI()
        .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
        .components(
            new Components()
                .addSchemas(
                    "ExchangeRate",
                    new Schema<Map<String, Object>>()
                        .addProperty("<USD, GBP>", new NumberSchema().example(1.20)))
                .addSchemas("ParameterMap", new Schema<Map<String, String>>().addProperty(SOURCE_ACCOUNT_ID, 
                                new StringSchema().example("1")).addProperty(TARGET_ACCOUNT_ID, new StringSchema().example("2")))
                .addSecuritySchemes(
                    securitySchemeName,
                    new SecurityScheme()
                        .name(securitySchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .description(
                            "Provide the JWT token. JWT token can be obtained from the \"authenticate\" endpoint.")
                        .bearerFormat("JWT")));
    }
}