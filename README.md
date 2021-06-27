# ToyFlow

## Intro

一个工作流管理引擎（服务）

Reference: [Designing a Workflow Engine Database](https://www.exceptionnotfound.net/designing-a-workflow-engine-database-part-1-introduction-and-purpose/)

(但该文章有些逻辑问题无法理解)

ToyFlow 能做什么?

- 流程管理
  - 流程拓扑结构存储和管理
  - 状态转移 `Action` (已实现) 和事件触发 `Activity` (还没实现) 管理
  - 流程（如审批）角色定义
- 流程运行
  - 以键值对的形式存储流程附属信息
  - 事件驱动的流程推动，即报告事件，该服务即返回当前状态, 下一步可以（应该）执行的 `Action` 和触发的 `Activity`

## Document

      
#### 概念

- **流程 (Process):** 是对一个流程如何执行的定义, 定义一个 Process 需要定义一个 Mealy 状态机的拓扑结构, 和如何推动流程进行
- **状态 (State):** 状态机中的节点
- **状态转移 (transition):** 状态机中的有向边
- **操作 (action):** 一个状态转移, 需要绑定至少一个操作, 绑定多个操作时表示需要这 **几个操作全部完成** 才能完成状态转移
- **操作组 (Group):** 流程中, 每个操作是需要特定的人来操作的, 如一个文件由 **客户经理** 起草, 再由 **分管副总, 营销总监** 审核通过, **客户经理, 分管副总** 和 **营销总监** 就是三个操作组

![](image/example-state-diagram.jpeg)

- **操作分发目标 (Action Target):** 操作的可执行范围是什么, 分为: 
  
  - `仅限发起者`
  - `仅限干系人`
  - `所有人`

  在确定谁可以执行一个操作时, 先确定当前 Action 的操作组, 再在该组内按照 Group Target 确定范围.
  
  与 Group 不同, Group 是随着不同 Process 而有不同定义的, 而 Target 是一个全局通用的静态划分, 对所有流程 (Process) 都是一样的
- **请求 (Request):** 一个运行中的流程实例, 也可以理解为在流程状态机上移动的游标, 它会附带一些数据, 称为 **Request Data**

#### 交互模型: 一个 request 的生命周期

![](image/流程的生命周期.png)

#### 基本使用

- 定义新流程
  - 定义拓扑结构(点: 状态, 边: 状态转移)
  - 定义 Activity (optional) 和 Action (每条边至少一个)
  - 定义流程角色组, 角色组多对多关联到用户
- 开启/推进流程
  - 定义 Request 
  - 附加 RequestData
  - 按照交互模型, 事件驱动

#### 数据层实体抽象设计 

![](image/e-r.jpg)
 