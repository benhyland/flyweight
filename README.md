Flyweight
=========

This is a design exercise and experiment in reading from a backing buffer in a reasonably structured way, with low garbage.

Any real attempt should probably be done via bytecode generation instead.

Semi-practical existing work along these lines: 

- [packed objects](http://duimovich.blogspot.co.uk/2012/11/packed-objects-in-java.html)
- [Slab](https://github.com/RichardWarburton/slab)
- [Javolution](http://javolution.org/)

More sensible in general might be to write code in C instead.

TODO PERHAPS

- use bytebuffer-backed CharSequence instead of String
- consider arrays
- consider nested structures
- give more complex examples
- implement other primitives
- try an off-heap backing buffer