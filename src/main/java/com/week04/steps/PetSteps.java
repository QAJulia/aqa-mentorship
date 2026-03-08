package com.week04.steps;

import com.week04.config.ApiConfig;
import com.week04.models.Pet;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.List;

import static io.restassured.RestAssured.given;

public class PetSteps {

    @Step("Create new pet")
    public Pet createPet(Pet pet) {
        Allure.addAttachment("Request Body", "application/json",
                String.format("Pet: %s, Status: %s", pet.getName(), pet.getStatus()));

        Pet created = given()
                .filter(new AllureRestAssured())
                .baseUri(ApiConfig.BASE_URI)
                .contentType(ContentType.JSON)
                .body(pet)
                .when()
                .post(ApiConfig.PET_ENDPOINT)
                .then()
                .statusCode(200)
                .extract().as(Pet.class);

        Allure.addAttachment("Created Pet ID", created.getId().toString());
        return created;
    }

    @Step("Create pet from JSON string")
    public Pet createPetFromJson(String jsonBody) {
        Allure.addAttachment("Request JSON", "application/json", jsonBody);

        Pet created = given()
                .filter(new AllureRestAssured())
                .baseUri(ApiConfig.BASE_URI)
                .contentType(ContentType.JSON)
                .body(jsonBody)
                .when()
                .post(ApiConfig.PET_ENDPOINT)
                .then()
                .statusCode(200)
                .extract().as(Pet.class);

        Allure.addAttachment("Created Pet ID", created.getId().toString());
        return created;
    }

    @Step("Get pet by ID: {petId}")
    public Pet getPetById(Long petId) {
        return given()
                .filter(new AllureRestAssured())
                .baseUri(ApiConfig.BASE_URI)
                .pathParam("petId", petId)
                .when()
                .get(ApiConfig.PET_BY_ID_ENDPOINT)
                .then()
                .statusCode(200)
                .extract().as(Pet.class);
    }

    @Step("Find pets by status: {status}")
    public List<Pet> findPetsByStatus(String status) {
        Response response = given()
                .filter(new AllureRestAssured())
                .baseUri(ApiConfig.BASE_URI)
                .queryParam("status", status)
                .when()
                .get(ApiConfig.PET_FIND_BY_STATUS_ENDPOINT)
                .then()
                .statusCode(200)
                .extract().response();

        List<Pet> pets = response.jsonPath().getList("", Pet.class);
        Allure.addAttachment("Pets Found", String.valueOf(pets.size()));
        return pets;
    }

    @Step("Update existing pet")
    public Pet updatePet(Pet pet) {
        Allure.addAttachment("Update Request", "application/json",
                String.format("Pet ID: %d, New Status: %s", pet.getId(), pet.getStatus()));

        return given()
                .filter(new AllureRestAssured())
                .baseUri(ApiConfig.BASE_URI)
                .contentType(ContentType.JSON)
                .body(pet)
                .when()
                .put(ApiConfig.PET_ENDPOINT)
                .then()
                .statusCode(200)
                .extract().as(Pet.class);
    }

    @Step("Update pet from JSON string")
    public Pet updatePetFromJson(String jsonBody) {
        Allure.addAttachment("Update JSON", "application/json", jsonBody);

        return given()
                .filter(new AllureRestAssured())
                .baseUri(ApiConfig.BASE_URI)
                .contentType(ContentType.JSON)
                .body(jsonBody)
                .when()
                .put(ApiConfig.PET_ENDPOINT)
                .then()
                .statusCode(200)
                .extract().as(Pet.class);
    }

    @Step("Delete pet by ID: {petId}")
    public void deletePet(Long petId) {
        given()
                .filter(new AllureRestAssured())
                .baseUri(ApiConfig.BASE_URI)
                .pathParam("petId", petId)
                .when()
                .delete(ApiConfig.PET_BY_ID_ENDPOINT)
                .then()
                .statusCode(200);

        Allure.addAttachment("Deleted Pet ID", petId.toString());
    }

    @Step("Verify pet does not exist: {petId}")
    public int getPetStatus(Long petId) {
        int statusCode = given()
                .filter(new AllureRestAssured())
                .baseUri(ApiConfig.BASE_URI)
                .pathParam("petId", petId)
                .when()
                .get(ApiConfig.PET_BY_ID_ENDPOINT)
                .then()
                .extract().statusCode();

        Allure.addAttachment("Status Code", String.valueOf(statusCode));
        return statusCode;
    }
}
