# Lexer
Lexer for WQ

## Code

```javascript
class class1(x,y,z) {
var lel = 12
var a = 10*2
var b = 10%4
var c = (10*4) - 2 + 3 /3
 if(1*2 < 3) {
        var e = 10
    } else {
        var l = 4 * (4 % a)
    }
}

class class2() {
    if(1*2 < 3) {
        var a = 10
    } else {
        var b = 2
    }
}
```

## Lexer

Tokens:

*class*, *class1*, *(*, *x*, *,*, *y*, *,*, *z*, *)*, *{*, *var*, *lel*, *=*, *12*, *var*, *a*, *=*, *10*, *, *2*, *var*, *b*, *=*, *10*, *%*, *4*, *var*, *c*, *=*, *(*, *10*, *, *4*, *)*, *-*, *2*, *+*, *3*, */*, *3*, *if*, *(*, *1*, *, *2*, *<*, *3*, *)*, *{*, *var*, *e*, *=*, *10*, *}*, *else*, *{*, *var*, *l*, *=*, *4*, *, *(*, *4*, *%*, *a*, *)*, *}*, *}*, *class*, *class2*, *(*, *)*, *{*, *if*, *(*, *1*, *, *2*, *<*, *3*, *)*, *{*, *var*, *a*, *=*, *10*, *}*, *else*, *{*, *var*, *b*, *=*, *2*, *}*, *}*, }

## Abstract Syntax Tree

```
│               ┌── 12 NumberNode
│           ┌── lel VarNode
│           │       ┌── 2 NumberNode
│           │   ┌── * Node
│           │   │   └── 10 NumberNode
│           └── a VarNode
│       ┌── [] ListNode
│       │   │       ┌── 4 NumberNode
│       │   │   ┌── % Node
│       │   │   │   └── 10 NumberNode
│       │   └── b VarNode
│       │   │           ┌── 3 NumberNode
│       │   │       ┌── / Node
│       │   │       │   └── 3 NumberNode
│       │   │   ┌── + Node
│       │   │   │   │   ┌── 2 NumberNode
│       │   │   │   └── - Node
│       │   │   │       │   ┌── 4 NumberNode
│       │   │   │       └── * Node
│       │   │   │           └── 10 NumberNode
│       │   └── c VarNode
│       │   │           ┌── 3 NumberNode
│       │   │       ┌── < Node
│       │   │       │   │   ┌── 2 NumberNode
│       │   │       │   └── * Node
│       │   │       │       └── 1 NumberNode
│       │   │   ┌── info Node
│       │   │   │   └── else ElseNode
│       │   │   │       └── [] ListNode
│       │   │   │           │           ┌── a KeyNode
│       │   │   │           │       ┌── % Node
│       │   │   │           │       │   └── 4 NumberNode
│       │   │   │           │   ┌── * Node
│       │   │   │           │   │   └── 4 NumberNode
│       │   │   │           └── l VarNode
│       │   └── if IfNode
│       │       └── [] ListNode
│       │           │   ┌── 10 NumberNode
│       │           └── e VarNode
│   ┌── class1 ClassNode
│   │   │   ┌── x KeyNode
│   │   └── [] ListNode
│   │       └── y KeyNode
│   │       └── z KeyNode
└── [] ListNode
    │   ┌── [] ListNode
    │   │   │           ┌── 3 NumberNode
    │   │   │       ┌── < Node
    │   │   │       │   │   ┌── 2 NumberNode
    │   │   │       │   └── * Node
    │   │   │       │       └── 1 NumberNode
    │   │   │   ┌── info Node
    │   │   │   │   └── else ElseNode
    │   │   │   │       └── [] ListNode
    │   │   │   │           │   ┌── 2 NumberNode
    │   │   │   │           └── b VarNode
    │   │   └── if IfNode
    │   │       └── [] ListNode
    │   │           │   ┌── 10 NumberNode
    │   │           └── a VarNode
    └── class2 ClassNode
        └── []

```
