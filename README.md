#Domain driven design in actor model using akka
This project tries to demonstrates how to design domain using actor model paradigm.  
I was drawing knowledge from great book with title [Applied Akka Patterns](http://shop.oreilly.com/product/0636920043577.do)
By Michael Nash and Wade Waldron.

#Domain Entities
Entity is something that is uniquely identifiable by key.  
Can be mutable, which means its state can change, but its key remains unaffected.  
Akka **actor** is entity. It's identified by its path or by its ID as key and its state can change e.g

```
  class User(id: UUID) extends Actor {
    override def receive: Receive = ...
  }
```
#Value Objects
Value objects are not identified.  
Are immutable - if their properties change, they become different value objects and are no longer equal.  
In Akka **messages** send between actors are natural value objects e.g.  

```
  object User {
    case class SetName(name: String)
  }
```
#Aggregates and Aggregate Roots
Aggregates are sort of logical grouping of many different elements of system.  
They are bound to aggregate root.  
Aggregate root is special entity that has responsibility for other members of that aggregate.  
In Akka aggregate roots are represented by **parent actor**, so when you delete this root, all children go with him.  
Example of aggregate root:

```
  object Player {
    def props = Props(new Player)
  }
  
  class Player extends Actor { ... }
  
  class Game(id: UUID) extends Actor {
    private val player = createPlayer()
    
    private def createPlayer() = context.actorOf(Player.props)
  }
```
#Repositories
Standard DDD theorems will tell you, that basic approach when working with aggregates, is to go to repository, retrieve
aggregate from repository, do some stuff and commit changes with repository again It seems like Ask pattern in 
Akka **BUT** Ask pattern violates principle of _"Tell, Don't Ask"_ that fits actor model best.
In Akka repositories might have different appearance. Instead of asking the repository for aggregate, you instruct 
repository to send a message to that aggregate, so now repository will act a "manager". You inform repository, 
that you want specific actor to process your message. The responsibility of repository is to locate that actor 
(or create it) and pass the message on.  
Since you want your implementation be part of infrastructure instead of domain, repositories in domain define only 
message protocol like so:

```
  object UserRepository {
    case class Send(userId: UUID, message: Any)
  }
```
Then your implementation in infrastructure might look like this:

```
  class JdbcSQLUserRepository extends Actor {
    override def receive: Receive = {
      case Send(id, message) => ...
    }
  }
```
#Factories
In Akka factories are very similar to repositories. Instead of Ask pattern, its better to use tell pattern 
where factory will create given actor and pass message to newly created instance.  
Because of its similarity to a repository, it doesn't make much sense to distinguish between them.

#TODO
- [ ] Domain Services
- [ ] Commands and Events
- [ ] Implement example of Domain Service
