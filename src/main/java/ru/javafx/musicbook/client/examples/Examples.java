
package ru.javafx.musicbook.client.examples;

public class Examples {
    
    /*
    // получить ресурс и потом у него же взять ссылки и получить из них другой ресурс
    public List<Resource<Genre>> getGenres(Resource<? extends Entity> artistResource) throws URISyntaxException {
        List<Resource<Genre>> genreResources = new ArrayList<>();
        Resources<Resource<ArtistGenre>> artistGenreResources = new Traverson(new URI(artistResource.getId().getHref()), MediaTypes.HAL_JSON)
                .follow("artistGenres")
                .withHeaders(sessionManager.createSessionHeaders())
                .toObject(new ParameterizedTypeReference<Resources<Resource<ArtistGenre>>>() {});
        
        artistGenreResources.getContent().parallelStream().forEach(artistGenre -> {
            try {
                Resource<Genre> genreResource = new Traverson(new URI(artistGenre.getId().getHref()), MediaTypes.HAL_JSON)
                        .follow("genre")
                        .withHeaders(sessionManager.createSessionHeaders())    
                        .toObject(new ParameterizedTypeReference<Resource<Genre>>() {});
                genreResources.add(genreResource);
            } catch (URISyntaxException ex) {
                //logger.error(ex.getMessage());
                ex.printStackTrace();
            }          
        });
        return genreResources;
    }
    */  

}
