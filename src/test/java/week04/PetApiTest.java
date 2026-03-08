package week04;

import com.week04.models.Category;
import com.week04.models.Pet;
import com.week04.models.Tag;
import com.week04.steps.PetSteps;
import com.week04.utils.JsonReader;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

@Epic("Petstore API")
@Feature("Pet Management")
public class PetApiTest extends BaseApiTest {

    private PetSteps petSteps;
    private Long testPetId;
    private Long jsonPetId;

    @BeforeClass
    public void setUp() {
        petSteps = new PetSteps();
        testPetId = System.currentTimeMillis(); // Unique ID
        jsonPetId = testPetId + 1000;
    }

    @Test(priority = 1, groups = {"smoke", "pets"})
    @Story("Create pet using POJO")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Create a new pet using POJO builder and verify it's created successfully")
    public void testCreatePetWithPOJO() {
        Pet newPet = Pet.builder()
                .id(testPetId)
                .name("Buddy")
                .category(Category.builder()
                        .id(1L)
                        .name("Dogs")
                        .build())
                .photoUrls(Arrays.asList("http://example.com/photo1.jpg"))
                .tags(Arrays.asList(Tag.builder()
                        .id(1L)
                        .name("friendly")
                        .build()))
                .status("available")
                .build();

        Pet created = petSteps.createPet(newPet);

        Assert.assertNotNull(created, "Created pet should not be null");
        Assert.assertEquals(created.getName(), "Buddy", "Pet name should match");
        Assert.assertEquals(created.getStatus(), "available", "Pet status should be available");
        Assert.assertNotNull(created.getCategory(), "Category should not be null");
        Assert.assertEquals(created.getCategory().getName(), "Dogs", "Category name should match");
    }

    @Test(priority = 2, groups = {"smoke", "pets", "json"})
    @Story("Create pet from JSON file")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Create a new pet using JSON from external file")
    public void testCreatePetFromJson() {
        // Read JSON from file
        String jsonPath = "src/test/resources/week04/testData/pet-create.json";
        Pet petFromJson = JsonReader.readJson(jsonPath, Pet.class);

        // Update ID to unique value
        petFromJson.setId(jsonPetId);

        // Create pet
        Pet created = petSteps.createPet(petFromJson);

        Assert.assertNotNull(created, "Created pet should not be null");
        Assert.assertEquals(created.getId(), jsonPetId, "Pet ID should match");
        Assert.assertEquals(created.getName(), "Fluffy", "Pet name should match JSON");
        Assert.assertEquals(created.getStatus(), "available", "Pet status should match JSON");
    }

    @Test(priority = 3, groups = {"smoke", "pets", "json"})
    @Story("Create pet from raw JSON string")
    @Severity(SeverityLevel.NORMAL)
    @Description("Create pet by sending raw JSON string as request body")
    public void testCreatePetFromRawJson() {
        String jsonPath = "src/test/resources/week04/testData/pet-create.json";
        String jsonBody = JsonReader.readJsonAsString(jsonPath);

        // Replace ID in JSON string
        jsonBody = jsonBody.replace("\"id\": 0", "\"id\": " + (jsonPetId + 100));

        Pet created = petSteps.createPetFromJson(jsonBody);

        Assert.assertNotNull(created, "Created pet should not be null");
        Assert.assertEquals(created.getName(), "Fluffy", "Pet name should match");
    }

    @Test(priority = 4, groups = {"smoke", "pets"}, dependsOnMethods = "testCreatePetWithPOJO")
    @Story("Get pet by ID")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Retrieve pet by ID and verify details")
    public void testGetPetById() {
        Pet pet = petSteps.getPetById(testPetId);

        Assert.assertNotNull(pet, "Retrieved pet should not be null");
        Assert.assertEquals(pet.getId(), testPetId, "Pet ID should match");
        Assert.assertEquals(pet.getName(), "Buddy", "Pet name should match");
        Assert.assertEquals(pet.getStatus(), "available", "Pet status should match");
    }

    @Test(priority = 5, groups = {"regression", "pets"})
    @Story("Find pets by status")
    @Severity(SeverityLevel.NORMAL)
    @Description("Find all available pets")
    public void testFindPetsByStatus() {
        List<Pet> pets = petSteps.findPetsByStatus("available");

        Assert.assertNotNull(pets, "Pets list should not be null");
        Assert.assertTrue(pets.size() > 0, "Should find at least one available pet");

        // Verify all returned pets have 'available' status
        for (Pet pet : pets) {
            Assert.assertEquals(pet.getStatus(), "available",
                    "All pets should have 'available' status");
        }
    }

    @Test(priority = 6, groups = {"regression", "pets"}, dependsOnMethods = "testGetPetById")
    @Story("Update pet using POJO")
    @Severity(SeverityLevel.NORMAL)
    @Description("Update pet name and status")
    public void testUpdatePetWithPOJO() {
        // Get existing pet
        Pet existingPet = petSteps.getPetById(testPetId);

        // Update fields
        existingPet.setName("Buddy Updated");
        existingPet.setStatus("sold");

        // Update pet
        Pet result = petSteps.updatePet(existingPet);

        Assert.assertEquals(result.getName(), "Buddy Updated", "Pet name should be updated");
        Assert.assertEquals(result.getStatus(), "sold", "Pet status should be sold");
    }

    @Test(priority = 7, groups = {"regression", "pets", "json"},
            dependsOnMethods = "testCreatePetFromJson")
    @Story("Update pet from JSON file")
    @Severity(SeverityLevel.NORMAL)
    @Description("Update pet using JSON from external file")
    public void testUpdatePetFromJson() {
        String jsonPath = "src/test/resources/week04/testData/pet-update.json";
        Pet updateData = JsonReader.readJson(jsonPath, Pet.class);

        // Set ID to existing pet
        updateData.setId(jsonPetId);

        Pet updated = petSteps.updatePet(updateData);

        Assert.assertEquals(updated.getId(), jsonPetId, "Pet ID should match");
        Assert.assertEquals(updated.getName(), "Fluffy Updated", "Pet name should be updated");
        Assert.assertEquals(updated.getStatus(), "pending", "Pet status should be pending");
    }

    @Test(priority = 8, groups = {"negative", "pets"})
    @Story("Get non-existent pet")
    @Severity(SeverityLevel.NORMAL)
    @Description("Try to get pet with non-existent ID, expect 404")
    public void testGetNonExistentPet() {
        int statusCode = petSteps.getPetStatus(999999999L);
        Assert.assertEquals(statusCode, 404, "Should return 404 for non-existent pet");
    }

    @AfterClass
    public void cleanup() {
        // Delete test pets
        try {
            petSteps.deletePet(testPetId);
        } catch (Exception e) {
            // Ignore cleanup errors
        }
    }
}
