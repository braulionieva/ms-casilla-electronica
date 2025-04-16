package pe.gob.mpfn.casilla.notifications.repository;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import pe.gob.mpfn.casilla.notifications.model.dto.SearchNotificationDto;
import pe.gob.mpfn.casilla.notifications.model.dto.SearchNotificationResponseDto;
import pe.gob.mpfn.casilla.notifications.model.dto.notification.ChangeFolderRequest;
import pe.gob.mpfn.casilla.notifications.model.dto.notification.NotificacionAdjunto;
import pe.gob.mpfn.casilla.notifications.model.dto.notification.NotificacionBandeja;
import pe.gob.mpfn.casilla.notifications.model.dto.notification.NotificacionDetalle;
import pe.gob.mpfn.casilla.notifications.util.DateUtils;
import pe.gob.mpfn.casilla.notifications.util.ShaUtils;
import pe.gob.mpfn.casilla.notifications.util.enums.Folder;
import pe.gob.mpfn.casilla.notifications.util.enums.ObjetosOracle;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.sql.Types.*;
import static pe.gob.mpfn.casilla.notifications.util.enums.AppParams.ROWS_PER_PAGE;
import static pe.gob.mpfn.casilla.notifications.util.enums.AppParams.SP_SUCCESSFUL_STATUS;
import static pe.gob.mpfn.casilla.notifications.util.enums.ObjetosOracle.*;

@RequiredArgsConstructor
public class CustomNotificationRepositoryImpl implements CustomNotificationRepository {

    private static final Logger log = LoggerFactory.getLogger(CustomNotificationRepositoryImpl.class);
    private final JdbcTemplate jdbcTemplate;

    @Override
    public SearchNotificationResponseDto getNotifications(SearchNotificationDto searchNotificationDto) {

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withoutProcedureColumnMetaDataAccess()
                .withSchemaName(SISCAS)
                .withProcedureName(CASP_GET_NOTIFICATIONS)
                .withCatalogName(CAPK_NOTIFICACION)
                .declareParameters(
                        new SqlParameter("PI_V_ID_CASILLA", VARCHAR),
                        new SqlParameter("PI_V_ID_PERSONA", VARCHAR),
                        new SqlParameter("PI_N_TIPO_SESION", VARCHAR),
                        new SqlParameter("PI_TRAMITE", VARCHAR),
                        new SqlParameter("PI_TAG", VARCHAR),
                        new SqlParameter("PI_ESTADO", VARCHAR),
                        new SqlParameter("PI_FOLDER", VARCHAR),
                        new SqlParameter("PI_ARCHIVADO", VARCHAR),
                        new SqlParameter("PI_FL_FOLDER", VARCHAR),
                        new SqlParameter("P_PAGE_SIZE", NUMERIC),
                        new SqlParameter("P_PAGE_NUMBER", NUMERIC),
                        new SqlOutParameter("P_CURSOR", REF_CURSOR, (RowMapper<NotificacionBandeja>) (rs, rowNum) ->
                             new NotificacionBandeja(
                                    rs.getString("ID_V_NOTIFICACION"),
                                    rs.getString("CO_V_CASO"),
                                    rs.getString("FL_C_TIPO_CEDULA"),
                                    rs.getString("NO_V_TIPO_URGENCIA"),
                                    rs.getString("NO_V_TIPO_PARTE_SUJETO"),
                                    rs.getString("STATUS"),
                                    rs.getString("NU_V_CEDULA"),
                                    rs.getString("NO_V_ETAPA"),
                                    rs.getString("NO_V_ACTO_PROCESAL"),
                                    rs.getString("NO_V_TRAMITE"),
                                    rs.getTimestamp("FE_D_ENVIO"),
                                    rs.getString("ID_N_CARPETA"),
                                    rs.getString("FL_C_ARCHIVADO"),
                                    rs.getString("NO_V_CIUDADANO_COMPLETO"),
                                    rs.getString("FL_C_DESTACADO"),
                                    rs.getString("FL_C_IMPORTANTE")
                            )

                        ),
                        new SqlOutParameter("PO_TOTAL_ROWS", NUMERIC),
                        new SqlOutParameter(PO_V_ERR_COD, VARCHAR),
                        new SqlOutParameter(PO_V_ERR_MSG, VARCHAR));

            String leido = null;
            String folder = null;
            String flFolder = null;
            String archivado = "--";

            if (Folder.LEIDO.getKey().equals(searchNotificationDto.getFolder())) {
                leido = "1";
            } else {
                folder = searchNotificationDto.getFolder();
            }

            if (Folder.DESTACADO.getKey().equals(searchNotificationDto.getFolder())
                || Folder.IMPORTANTE.getKey().equals(searchNotificationDto.getFolder())) {
                flFolder = searchNotificationDto.getFolder();
            }

            if (Folder.ARCHIVADOS.getKey().equals(searchNotificationDto.getFolder())) {
                archivado = Folder.ARCHIVADOS.getKey();
                folder = null;
            }
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("PI_V_ID_CASILLA", ShaUtils.sanitizeInput(searchNotificationDto.getIdCasilla()))
                .addValue("PI_V_ID_PERSONA", ShaUtils.sanitizeInput(searchNotificationDto.getIdPersona()))
                .addValue("PI_N_TIPO_SESION", ShaUtils.sanitizeInput(searchNotificationDto.getSessionType()))
                .addValue("PI_TRAMITE", ShaUtils.sanitizeInput(searchNotificationDto.getTramite()))
                .addValue("PI_TAG", ShaUtils.sanitizeInput(searchNotificationDto.getTag()))
                .addValue("PI_ESTADO", ShaUtils.sanitizeInput(leido))
                .addValue("PI_FOLDER", ShaUtils.sanitizeInput(folder))
                .addValue("PI_ARCHIVADO", ShaUtils.sanitizeInput(archivado))
                .addValue("PI_FL_FOLDER", ShaUtils.sanitizeInput(flFolder))
                .addValue("P_PAGE_SIZE", searchNotificationDto.getPageSize())
                .addValue("P_PAGE_NUMBER", searchNotificationDto.getPageNumber());

        Map<String, Object> res = jdbcCall.execute(params);

        if (!SP_SUCCESSFUL_STATUS.getValue().equals(res.get(PO_V_ERR_COD).toString())) {
            log.info("request: {}", searchNotificationDto);
            log.info("params: {}",params);
            log.info("{}", res.get(PO_V_ERR_MSG));
        }

        var list = Optional.ofNullable((List<?>) res.get("P_CURSOR")).orElseGet(ArrayList::new);

        return new SearchNotificationResponseDto(list, Integer.parseInt(res.get("PO_TOTAL_ROWS").toString()), ROWS_PER_PAGE.getIntVal());
    }

    public Optional<NotificacionDetalle> obtenerNotificacion(String idCasilla, String idNotificacion) {
        var simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName(ObjetosOracle.SISCAS)
                .withProcedureName(ObjetosOracle.CASP_OBTENER_NOTIFICACION)
                .withCatalogName(CAPK_NOTIFICACION)
                .declareParameters(
                        new SqlParameter("PI_V_ID_PERSONA", Types.VARCHAR),
                        new SqlParameter("PI_V_ID_NOTIFICACICON", Types.VARCHAR),
                        new SqlOutParameter("P_CURSOR", REF_CURSOR, new RowMapper<NotificacionDetalle>() {

                            @Override
                            public NotificacionDetalle mapRow(ResultSet rs, int rowNum) throws SQLException {
                                String idNotificacion = rs.getString("ID_V_NOTIFICACION");
                                String ciudadanoCompleto = rs.getString("NO_V_CIUDADANO_COMPLETO");
                                Timestamp fechaEnvio = rs.getTimestamp("FE_D_ENVIO"); // or rs.getTimestamp("FE_D_ENVIO") if you need time as well
                                String cedula = rs.getString("NU_V_CEDULA");
                                String tipoCedula = rs.getString("FL_C_TIPO_CEDULA");
                                String despacho = rs.getString("NO_V_DESPACHO");
                                String caso = rs.getString("CO_V_CASO");
                                String entidad = rs.getString("NO_V_ENTIDAD");
                                String tipoDomicilio = rs.getString("NO_V_TIPO_DOMICILIO");
                                String idTipoDomicilio = rs.getString("ID_N_TIPO_DOMICILIO");
                                String archivado = rs.getString("FL_C_ARCHIVADO");
                                String status = rs.getString("STATUS");

                                return new NotificacionDetalle(
                                        idNotificacion,
                                        ciudadanoCompleto,
                                        fechaEnvio, cedula,
                                        tipoCedula, despacho, caso, entidad, tipoDomicilio, idTipoDomicilio, status, archivado);
                            }
                        }),
                        new SqlOutParameter(PO_V_ERR_COD, Types.VARCHAR),
                        new SqlOutParameter(PO_V_ERR_MSG, Types.VARCHAR)
                );

        Map<String, Object> out = simpleJdbcCall.execute(
                Map.of(
                        "PI_V_ID_CASILLA", idCasilla,
                        "PI_V_ID_NOTIFICACICON", idNotificacion
                ));

        if (!SP_SUCCESSFUL_STATUS.getValue().equals(out.get(PO_V_ERR_COD).toString())) {
            log.info("idCasilla: {} - idNotificacion {}", idCasilla, idNotificacion);
            log.info("{}", out.get(PO_V_ERR_MSG));
        }

        var list = Optional.ofNullable((List<NotificacionDetalle>) out.get("P_CURSOR")).orElseGet(ArrayList::new);

        return list.stream().findFirst();
    }


    public List<NotificacionAdjunto> obtenerAdjuntos(String idNotificacion) {
        var simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName(ObjetosOracle.CASP_OBTENER_ADJUNTOS)
                .withSchemaName(ObjetosOracle.SISCAS)
                .withCatalogName(CAPK_NOTIFICACION)
                .declareParameters(
                        new SqlParameter("PI_V_ID_NOTIFICACICON", Types.VARCHAR),
                        new SqlOutParameter("P_CURSOR", REF_CURSOR, (RowMapper<NotificacionAdjunto>) (rs, rowNum) -> {
                            String notificationAttachmentId = rs.getString("ID_V_NOTIFICACION_ADJUNTO");
                            String originalDocumentName = rs.getString("NO_V_DOCUMENTO_ORIGEN");
                            String documentCode = rs.getString("CO_V_DOCUMENTO");
                            String documentId = rs.getString("ID_V_DOCUMENTO");
                            var numeroOrden = rs.getInt("NU_N_ORDEN");
                            var fechaCreacion = rs.getTimestamp("FE_D_CREACION");

                            if (numeroOrden != 1 && DateUtils.isDateOlderThan180Days(fechaCreacion)) {
                                documentId = "";
                            }

                            return new NotificacionAdjunto(
                                    notificationAttachmentId,
                                    documentId,
                                    documentCode,
                                    originalDocumentName,
                                    fechaCreacion,
                                    numeroOrden
                            );
                        }),
                        new SqlOutParameter(PO_V_ERR_COD, Types.VARCHAR),
                        new SqlOutParameter(PO_V_ERR_MSG, Types.VARCHAR)
                );

        Map<String, Object> out = simpleJdbcCall.execute(
                Map.of("PI_V_ID_NOTIFICACICON", idNotificacion)
        );

        if (!SP_SUCCESSFUL_STATUS.getValue().equals(out.get(PO_V_ERR_COD).toString())) {
            log.info("idNotificacion {}", idNotificacion);
            log.info("{}", out.get(PO_V_ERR_MSG));
        }

        return Optional.ofNullable((List<NotificacionAdjunto>) out.
                get("P_CURSOR")).orElseGet(ArrayList::new);
    }

    public boolean actualizarEstadoLeido(String idNotificacion) {
        var simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName(CASP_REGISTRAR_ESTADO_LEIDO)
                .withSchemaName(ObjetosOracle.SISCAS)
                .withCatalogName(CAPK_NOTIFICACION)
                .declareParameters(
                        new SqlParameter("PI_V_ID_NOTIFICACION", Types.VARCHAR),
                        new SqlOutParameter(PO_V_ERR_COD, Types.VARCHAR),
                        new SqlOutParameter(PO_V_ERR_MSG, Types.VARCHAR)
                );

        Map<String, Object> out = simpleJdbcCall.execute(
                Map.of("PI_V_ID_NOTIFICACICON", idNotificacion)
        );

        if (!SP_SUCCESSFUL_STATUS.getValue().equals(out.get(PO_V_ERR_COD).toString())) {
            log.info("actualizar estado idNotificacion {}", idNotificacion);
            log.info("{}", out.get(PO_V_ERR_MSG));
        }

        return SP_SUCCESSFUL_STATUS.getValue().equals(out.getOrDefault(PO_V_ERR_COD,"1"));
    }

    @Override
    public boolean archivarNotificaciones(String personaId, List<String> idNotificacion) {

            var simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withSchemaName(ObjetosOracle.SISCAS)
                    .withProcedureName(CASP_ARCHIVAR_NOTIFICACION)
                    .withCatalogName(CAPK_NOTIFICACION)
                    .declareParameters(
                            new SqlParameter("PI_V_ID_PERSONA", Types.VARCHAR),
                            new SqlParameter("PI_V_ID_NOTIFICACION", Types.VARCHAR),
                            new SqlOutParameter(PO_V_ERR_COD, Types.VARCHAR),
                            new SqlOutParameter(PO_V_ERR_MSG, Types.VARCHAR)
                    );

            Map<String, Object> out = simpleJdbcCall.execute(
                    Map.of(
                            "PI_V_ID_PERSONA", personaId,
                            "PI_V_ID_NOTIFICACION", String.join(",", idNotificacion)
                    ));

        if (!SP_SUCCESSFUL_STATUS.getValue().equals(out.get(PO_V_ERR_COD).toString())) {
            log.info("Archivar {}", idNotificacion);
            log.info("{}", out.get(PO_V_ERR_MSG));
        }

        return SP_SUCCESSFUL_STATUS.getValue().equals(out.get(PO_V_ERR_COD).toString());

    }

    @Override
    public boolean cambiarFlagNotificacion(ChangeFolderRequest folderRequest) {
        var simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName(ObjetosOracle.SISCAS)
                .withProcedureName(CASP_ACTUALIZAR_FLAG)
                .withCatalogName(CAPK_NOTIFICACION)
                .declareParameters(
                        new SqlParameter("PI_V_CASILLA", VARCHAR),
                        new SqlParameter("PI_V_FLAG", VARCHAR),
                        new SqlParameter("PI_V_ID_NOTIFICACION", VARCHAR),
                        new SqlOutParameter(PO_V_ERR_COD, VARCHAR),
                        new SqlOutParameter(PO_V_ERR_MSG, VARCHAR)
                );

        Map<String, Object> out = simpleJdbcCall.execute(
                Map.of(
                        "PI_V_CASILLA", folderRequest.getCasillaId(),
                        "PI_V_FLAG", folderRequest.getFolderValue(),
                        "PI_V_ID_NOTIFICACION", String.join(",", folderRequest.getNotifId())
                ));

        if (!SP_SUCCESSFUL_STATUS.getValue().equals(out.get(PO_V_ERR_COD).toString())) {
            log.info("cambiar status {}", folderRequest);
            log.info("{}", out.get(PO_V_ERR_MSG));
        }

        return "0".equals(out.get(PO_V_ERR_COD).toString());
    }
}
