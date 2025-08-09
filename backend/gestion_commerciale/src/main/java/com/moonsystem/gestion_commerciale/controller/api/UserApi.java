package com.moonsystem.gestion_commerciale.controller.api;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;

import com.moonsystem.gestion_commerciale.dto.UserDto;
import static com.moonsystem.gestion_commerciale.utils.Constants.APP_ROOT;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

public interface UserApi {

    @GetMapping(value = APP_ROOT + "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Lister les utilisateurs disponibles",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Liste des Utilisateurs",
                        content = @Content(
                                mediaType = MediaType.APPLICATION_JSON_VALUE,
                                array = @ArraySchema(schema = @Schema(implementation = UserDto.class))
                        )
                )
            }
    )
    List<UserDto> getAllUsers();
}
