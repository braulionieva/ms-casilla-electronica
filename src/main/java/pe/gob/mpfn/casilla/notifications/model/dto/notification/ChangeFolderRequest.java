package pe.gob.mpfn.casilla.notifications.model.dto.notification;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ChangeFolderRequest {

    private String casillaId;
    @NotBlank
    private List<String> notifId = new ArrayList<>();
    @NotBlank
    private String folderValue;
}
