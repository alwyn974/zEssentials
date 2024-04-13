package fr.maxlego08.essentials.storage.database.repositeries;

import fr.maxlego08.essentials.api.database.dto.HomeDTO;
import fr.maxlego08.essentials.api.home.Home;
import fr.maxlego08.essentials.storage.database.Repository;
import fr.maxlego08.essentials.storage.database.SqlConnection;

import java.util.List;
import java.util.UUID;

public class UserHomeRepository extends Repository {

    public UserHomeRepository(SqlConnection connection) {
        super(connection, "player_homes");
    }

    public void upsert(UUID uuid, Home home) {
        upsert(table -> {
            table.uuid("unique_id", uuid);
            table.string("name", home.getName());
            table.string("location", locationAsString(home.getLocation()));
        });
    }

    public List<HomeDTO> selectHomes(UUID uuid) {
        return select(HomeDTO.class, schema -> schema.where("unique_id", uuid));
    }

    public void deleteHomes(UUID uuid, String name) {
        delete(table -> table.where("unique_id", name).where("name", name));
    }
}