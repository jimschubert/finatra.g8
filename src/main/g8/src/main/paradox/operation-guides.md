# Operation Guides

## System Environment Variables

| Variable                   | Description                                                                       | Default                   |
| -------------------------- | --------------------------------------------------------------------------------- | ------------------------- |
| `FINATRA_HTTP_PORT`        | The HTTP port the service uses                                                    | 9999                      |
| `LOG_LEVEL`                | The log level used by the service                                                 | INFO                      |
| `TLS_VALIDATION`           | Should the HTTP verify TLS? The default behaviour is not to verify TLS            | false                     |
| `FAIL_FAST_ENABLE`         | Should the HTTP client fail at once the remote does not respond expectedly?       | false                     |
| `SWAGGER_DOC_PATH`         | the URL path to Swagger document                                                  | $swagger_doc_path$ |
