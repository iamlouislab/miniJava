Architecture du projet: 
Nous avons ajouté les nouvelles classes dans le package miniJava qui permettent de gérer les Classes, Attributs et tout autres éléments spécifiques à miniJava.
Nous avons modifié le parser pour utiliser ces nouvelles classes. Pour la gestion de la table des symboles et les définitions des différentes classes nous avons utilisé des attributs statiques.
Enfin pour gérer les surcharges de méthodes nous avons utilisé le calcul d'une signature unique en utilisant les différents types des paramètres pour pouvoir différencier les appels.


Il y a un problème avec la gestion de la table de symboles lors de l'appel des méthodes dans les classes VoidMethod et MethodCall. 
Cela est lié au fait que lors de l'utilisation du test_method.java, on peut afficher le contenu de la table des symboles avec l'attribut a1 qui est bien défini mais
lorsque l'on utilise _scope.knows(this.callerObject.toString()) on obtient false ce qui renvoie alors au cas ou on utilise un attribut non défini. 

Nous avons testé de changer le nom de l'attribut en enlevant des espaces mais cela ne change rien. Ce qui est etrange est qu'en testant avec un nom "hardcodé" avec
_scope.knows("a1") cela fonctionne. En théorie le reste de l'analyse sémantique devrait fonctionner mais nous n'avons pas pu le tester.