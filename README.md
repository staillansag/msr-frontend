# dceFrontEnd

Ce package gère les échanges orientées API pour les différents cas d'usages.

## Utilisation sur EKS

Jusqu'à nouvel ordre, il faut faire un port-forward du service msr-dce-frontend (solution de contournement tant que les ingresses EKS ne sont pas au point):
```
kubectl port-forward svc/msr-dce-frontend 8080:80
```
La commande doit retourner ceci:
```
Forwarding from 127.0.0.1:8080 -> 5555
Forwarding from [::1]:8080 -> 5555
```
Une fois ce port-forward effectué, on peut faire des appels d'API en pointant sur localhost.
Attention: ces appels en localhost ne fonctionnent que sur la machine où me port-forward a été effectué. On met en place une sorte de tunnel ssh entre la machine locale et le cluster EKS. 


### cas d'usage 1 - demande de fichier zip des personnes

```
curl --location --request POST 'http://localhost:8080/personnesAPI/personnes/demande-zip' \
--header 'Authorization: Basic QWRtaW5pc3RyYXRvcjptYW5hZ2U='
```
La réponse de l'API doit ressembler à ceci:
```
{
    "idDemande": "c5819c4d-40a6-4475-b4f5-4a9c4aa809ad"
}
```