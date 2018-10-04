# CodeGenDemo
Im vorliegenden Projekt ist eine Referenzanwendung enthalten, welche mit dem Java Modulsystem erstellt wurde. 
Diese verfügt über eine Modul mit der Bezeichnung app, welche unterschiedliche Services über den Service-Mechanismus 
des Modulsystems aufruft. Die Schnittstellen der Services sind in dem Modul service.api gekapselt.  
Die Anwendung enthält zwei unterschiedliche Serviceinterfaces. Zum einen das Intverface mit der Bezeichnung CalcService,
welches von den drei Modulen  service.a, service.b und service.D  implementiert wird. Zum anderen gibt es das SayHelloService Interface, 
welches lediglich über eine Implementierung in Modul service.c verfügt.

Die modular entwickelte Anwendung wurde mit dem in der Arbeitmit dem Titel "Konzeption und Implementierung eines Prototyps zur 
automatisierten Isolation von Software-Modulen" erstellten Prototyp, in eine containerbasierte Anwendung überführt.
Aufgrund der definierten Isolationsbedingungen, welche in den Modulen in der isomod.json Datei entahlten sind, wurden dabei die Module 
service.a und service.b von der Anwendung isoliert. Die Zwischenergebnisse, sowie alle für die Erstellung der Laufzeitumgebung benötigten
Objekte sind wie in der angegebenen Arbeit beschrieben im Verzeichnis generatedFiles enthalten.
