package pe.gob.mpfn.casilla.notifications.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;
import pe.gob.mpfn.casilla.notifications.exception.HttpStatusResponseException;
import pe.gob.mpfn.casilla.notifications.exception.InternalServerErrorException;
import pe.gob.mpfn.casilla.notifications.model.dto.notification.NotificacionAdjunto;
import pe.gob.mpfn.casilla.notifications.util.enums.ObjetosOracle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.sql.Types.REF_CURSOR;
import static java.sql.Types.VARCHAR;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static pe.gob.mpfn.casilla.notifications.util.enums.AppParams.SP_ERROR_STATUS;
import static pe.gob.mpfn.casilla.notifications.util.enums.AppParams.SP_INTERNAL_ERROR;
import static pe.gob.mpfn.casilla.notifications.util.enums.ObjetosOracle.PO_V_ERR_COD;
import static pe.gob.mpfn.casilla.notifications.util.enums.ObjetosOracle.PO_V_ERR_MSG;

@Repository
public class NotificacionAdjuntoRepository {

    private static final Logger log = LoggerFactory.getLogger(NotificacionAdjuntoRepository.class);
    private final JdbcTemplate jdbcTemplate;

    public NotificacionAdjuntoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<NotificacionAdjunto> obtenerAdjunto(String casillaId, String notificacionId, String documentoId) {

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate).withoutProcedureColumnMetaDataAccess()
                .withSchemaName(ObjetosOracle.SISCAS)
                .withCatalogName(ObjetosOracle.CAPK_NOTIFICACION)
                .withProcedureName(ObjetosOracle.CASP_OBTENER_ADJUNTO)
                .declareParameters(
                        new SqlParameter("PI_V_ID_NOTIFICACICON", VARCHAR),
                        new SqlParameter("PI_V_ID_CASILLA", VARCHAR),
                        new SqlParameter("PI_V_ID_DOCUMENTO", VARCHAR),
                        new SqlOutParameter("P_CURSOR", REF_CURSOR, (RowMapper<NotificacionAdjunto>) (rs, rowNum) -> {
                            var numeroOrden = rs.getInt("NU_N_ORDEN");
                            var fechaCreacion = rs.getTimestamp("FE_D_CREACION");
                            var idNotificacionAdjunto = rs.getString("ID_V_NOTIFICACION_ADJUNTO");
                            var idDocumento = rs.getString("ID_V_DOCUMENTO");
                            return new NotificacionAdjunto(
                                    numeroOrden,
                                    fechaCreacion,
                                    idNotificacionAdjunto,
                                    idDocumento
                            );
                        } ),
                        new SqlOutParameter(PO_V_ERR_COD, VARCHAR),
                        new SqlOutParameter(PO_V_ERR_MSG, VARCHAR)
                );

        SqlParameterSource objParametrosIN = new MapSqlParameterSource()
                .addValue("PI_V_ID_NOTIFICACICON", notificacionId)
                .addValue("PI_V_ID_CASILLA", casillaId)
                .addValue("PI_V_ID_DOCUMENTO", documentoId);

        Map<String, Object> res = jdbcCall.execute(objParametrosIN);

        if (SP_INTERNAL_ERROR.getValue().equals(res.get(PO_V_ERR_COD))) {
            log.error("casillaID {} - {}", casillaId, res.get(PO_V_ERR_MSG));
            throw new InternalServerErrorException();
        }
        if (SP_ERROR_STATUS.getValue().equals(res.get(PO_V_ERR_COD))) {
            log.info("casillaID {} - {}", casillaId, res.get(PO_V_ERR_MSG));
            throw new HttpStatusResponseException(BAD_REQUEST, res.get(PO_V_ERR_MSG).toString());
        }
        return Optional.ofNullable((List<NotificacionAdjunto>) res.
                get("P_CURSOR")).orElseGet(ArrayList::new).stream().findFirst();

    }

}
