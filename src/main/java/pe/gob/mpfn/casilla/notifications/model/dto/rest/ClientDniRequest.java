package pe.gob.mpfn.casilla.notifications.model.dto.rest;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ClientDniRequest {

    private String clienteIp;
    private String httpHost;
    private String httpUserAgent;
    private String typeBrowser;
    private String browser;
    private String versionBrowser;
    private String parent;
    private String platform;
    private String platformVersion;
    private String numeroDocumento;
    private String ip;

}
